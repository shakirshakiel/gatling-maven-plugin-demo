package computerdatabase

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class InfraSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("https://app.example.com")

  def getTestEndpoint() = exec(http("get test ep").get("/infra/go/test"))

  def getSlowEndpoint() = exec(http("get test ep").get("/infra/go/retry"))

  def postTestEndpoint() = exec(
    http("post test ep").post("/infra/go/post")
    .bodyPart(RawFileBodyPart("raw", "src/test/resources/infra.raw").fileName("infra.raw").transferEncoding("binary"))
    .asMultipartForm)

  var runOutOfSockets = scenario("Run out of sockets")
    .exec(getTestEndpoint())
    .exec(postTestEndpoint())
    .exec(getSlowEndpoint())

  setUp(
    runOutOfSockets.inject(constantConcurrentUsers(50) during (15 minutes))
    .protocols(httpProtocol)
    )
}