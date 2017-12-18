import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class Ac3 extends Simulation {
  
  val initiate = exec(http("Initiate")
    .post("${host}/voicexml/rest-integration/twiliocallout/initiate")
    .queryParamMap(Map(
        "requireAuthentication" -> "false",
        "ntfnId" -> "${ntfnId}",
        "nodeId" -> "${nodeId}"))
    .formParamMap(Map(
        "Called" -> "",
        "CallStatus" -> "in-progress"))
    .check(status.is(200)))

  val welcome = exec(http("Welcome")
    .get("${host}/voicexml/rest-integration/twiliocallout/WELCOME")
    .queryParamMap(Map(
        "requireAuthentication" -> "false",
        "ntfnId" -> "${ntfnId}",
        "nodeId" -> "${nodeId}",
        "Direction" -> "outbound-api",
        "CallStatus" -> "in-progress"))
    .check(status.is(200)))

  val content = exec(http("Content")
    .get("${host}/voicexml/rest-integration/twiliocallout/CONTENT")
    .queryParamMap(Map(
        "requireAuthentication" -> "false",
        "ntfnId" -> "${ntfnId}",
        "nodeId" -> "${nodeId}",
        "deviceId" -> "${deviceId}",
        "orgId" -> "${orgId}",
        "orgName" -> "${orgName}",
        "eventId" -> "${eventId}",
        "personId" -> "${personId}",
        "Digits" -> "1",
        "invalidResponseRetries" -> "0",
        "noResponseRetries" -> "0",
        "language" -> "English",
        "stepUri" -> "PRESS_ANY_DIGIT",
        "voicemailOptions" -> "CALLBACK",
        "isRebValidation" -> "false",
        "retries" -> "0",
        "isDevMessage" -> "false",
        "msg" -> "Gather End",
        "Direction" -> "outbound-api",
        "CallStatus" -> "in-progress"))
    .check(status.is(200)))

  val response = exec(http("Response")
    .get("${host}/voicexml/rest-integration/twiliocallout/RESPONSE")
    .queryParamMap(Map(
        "requireAuthentication" -> "false",
        "ntfnId" -> "${ntfnId}",
        "nodeId" -> "${nodeId}",
        "deviceId" -> "${deviceId}",
        "orgId" -> "${orgId}",
        "orgName" -> "${orgName}",
        "eventId" -> "${eventId}",
        "personId" -> "${personId}",
        "Digits" -> "2",
        "invalidResponseRetries" -> "0",
        "language" -> "English",
        "noResponseRetries" -> "0",
        "stepUri" -> "CONTENT",
        "voicemailOptions" -> "CALLBACK",
        "isRebValidation" -> "false",
        "retries" -> "0",
        "isDevMessage" -> "false",
        "msg" -> "Gather End",
        "Direction" -> "outbound-api",
        "CallStatus" -> "in-progress"))
    .check(status.is(200)))

  val httpConf = http
    .basicAuth("censored", "censored")

  val scn = scenario("AC3")
    .exec(
      feed(csv("ntfns.csv")),
      initiate,
      pause(1.second, 2.second), // random 1-2 sec pause
      welcome,
      pause(1.second, 2.second),
      content,
      pause(1.second, 2.second),
      response)

  setUp(scn.inject(rampUsers(500).over(30.seconds))).protocols(httpConf)
}