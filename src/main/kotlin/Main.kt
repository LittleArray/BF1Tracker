import api.Api
import data.CurrentGamingServer
import data.GatewayPID
import kotlinx.coroutines.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class Main

var cs = CoroutineScope(Dispatchers.IO)
var jobs = mutableSetOf<String>()
val logger: Logger = LoggerFactory.getLogger("Tracker")
fun main(args: Array<String>) {
    logger.debug("Program arguments: ${args.joinToString()}")
    println("""
        
        BF1Tracker v1 by LittleArray
        使用方法,输入需要跟踪玩家的ID即可
        .rm 移除全部跟踪
        .cdn 无法连接网络使用该命令
    """)
    val scanner = Scanner(System.`in`)
    while (true) {
        cmd(scanner.nextLine().split(" "))
    }
}

fun cmd(cmd: List<String>) {
    cmd.getOrNull(0)?.let { id ->
        if (id == ".rm"){
            jobs = mutableSetOf()
            logger.info("全部跟踪已被移除")
            return
        }
        if (id == ".cdn"){
            if(Api.serverUrl.contains("ipv6")){
                Api.serverUrl = "http://ffshaozi.top:8080"
            }else{
                Api.serverUrl = "http://ipv6.ffshaozi.top:8080"
            }
            logger.info("切换CDN成功")
            return
        }
        if (jobs.any { id == it }){
            jobs.remove(id)
            logger.info("移除跟踪玩家 {}",id)
        } else {
            cs.launch {
                Api.getUser(id)?.let {
                    Api.fromJson<GatewayPID>(it)?.let {gwi->
                        gwi.personas?.persona?.firstOrNull()?.personaId?.let { pid ->
                            logger.info("搜索玩家成功 PID:{} ID:{} 最后上线时间:{}", pid, id,gwi.personas.persona.first().lastAuthenticated)
                            jobs.add(id).takeIf { it }.let {
                                var oldPlay = ""
                                while (jobs.any { id == it }) {
                                    Api.getNowPlay(pid)?.let { data ->
                                        logger.debug("更新玩家 {} 游玩信息", id)
                                        Api.fromJson<CurrentGamingServer>(data)?.let {
                                            it.result?.firstNotNullOfOrNull {
                                                it.value?.gameId?.takeIf { it != oldPlay }?.let { gameID ->
                                                    logger.info(
                                                        "玩家 {} 离开 {} 前往 {} {} {}-{} {}/{}[{}]({})",
                                                        id,
                                                        oldPlay,
                                                        gameID,
                                                        it.value?.name,
                                                        it.value?.mapNamePretty,
                                                        it.value?.mapModePretty,
                                                        it.value?.slots?.Soldier?.current,
                                                        it.value?.slots?.Soldier?.max,
                                                        it.value?.slots?.Queue?.current,
                                                        it.value?.slots?.Spectator?.current,
                                                    )
                                                }
                                                it.value?.gameId?.let {
                                                    oldPlay = it
                                                }
                                            }
                                            if (it.result?.getOrDefault(pid,null) == null && oldPlay != "大厅"){
                                                oldPlay = "大厅"
                                                logger.info("玩家 {} 正在游戏大厅或离线",id)
                                            }
                                        }
                                    }
                                    delay(15000)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}