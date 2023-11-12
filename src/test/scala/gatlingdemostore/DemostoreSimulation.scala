package gatlingdemostore

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class DemostoreSimulation extends Simulation {


  val domain = "demostore.gatling.io"

  val httpProtocol = http
    .baseUrl("https://" + domain)


  object CmsPage {
    def homePage = {
    exec(http("Load Home Page")
      .get("/")
      .check(status.is(200))
      .check(css("#_csrf", "content").saveAs("csrfValue"))
      )
    }

    def aboutUs  = {
    exec(http("Load About us Page")
      .get("/about-us")
      .check(status.is(200))
      .check(substring("About Us"))

      )
    }


  }



  val scn = scenario("RecordedSimulation")
    .exec(CmsPage.homePage)
    .pause(2)
    .exec(CmsPage.aboutUs)
    .pause(2)
    .exec(http("Load Categories Page")
      .get("/category/all")
    )
    .pause(2)
    .exec(http("Load Product Page")
      .get("/product/black-and-red-glasses")
    )
    .pause(2)
    .exec(http("Add Product to Cart")
      .get("/cart/add/19")
    )
    .pause(2)
    .exec(http("View Cart")
      .get("/cart/view")
    )
    .pause(2)
    .exec(http("Login User")
      .post("/login")
      .formParam("_csrf", "${csrfValue}")
      .formParam("username", "user1")
      .formParam("password", "pass"))
    .pause(2)
    .exec(http("Checkout")
      .get("/cart/checkout")
    )

  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
