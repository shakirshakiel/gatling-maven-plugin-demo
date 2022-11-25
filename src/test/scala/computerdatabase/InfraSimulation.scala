package computerdatabase

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class InfraSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl(s"${sys.env("DOMAIN")}")

  def getTestEndpoint() = exec(http("get test ep").get("/beacon"))

  var runOutOfSockets = scenario("Run out of sockets")
    .exec(getTestEndpoint())

  setUp(
    runOutOfSockets.inject(constantConcurrentUsers(50) during (60 minutes))
    .protocols(httpProtocol)
    )
}