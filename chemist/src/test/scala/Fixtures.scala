package funnel
package chemist

import scalaz.==>>
import java.net.URI
import java.util.UUID
import org.scalacheck.{ Arbitrary, Gen }
import org.scalacheck.Prop.BooleanOperators

object Fixtures {
  import Sharding.Distribution
  import LocationIntent._
  import Arbitrary.arbitrary
  import Gen.{ alphaNumChar, listOf, listOfN, oneOf }

  implicit lazy val arbDistribution: Arbitrary[Distribution] = Arbitrary(genDistribution)
  implicit lazy val arbUUID: Arbitrary[UUID] = Arbitrary(genUUID)
  implicit lazy val arbURI: Arbitrary[URI] = Arbitrary(genURI)
  implicit lazy val arbTarget: Arbitrary[Target] = Arbitrary(genTarget)
  implicit lazy val arbLocationIntent: Arbitrary[LocationIntent] = Arbitrary(oneOf(Mirroring, Ignored))
  implicit lazy val arbLocationTemplate: Arbitrary[LocationTemplate] = Arbitrary(genLocationTemplate)
  implicit lazy val arbLocation: Arbitrary[Location] = Arbitrary(genLocation)
  implicit lazy val arbFlaskID: Arbitrary[FlaskID] = Arbitrary(genFlaskID)
  implicit lazy val arbFlask: Arbitrary[Flask] = Arbitrary(genFlask)

  /** Generates alphanumeric characters */
  def alphaNumStr: Gen[String] =
    listOfN(10, alphaNumChar).map(_.mkString).suchThat(_.forall(c => c.isDigit || c.isLetter))

  def genUUID: Gen[UUID] =
    UUID.randomUUID

  def genURI: Gen[URI] = for {
    str <- alphaNumStr
  } yield new URI("http://localhost:8080/chemist/" + str)

  def genTarget: Gen[Target] = for {
    id  <- arbitrary[UUID]
    uri <- arbitrary[URI]
  } yield Target(id.toString, uri)

  def genLocationTemplate: Gen[LocationTemplate] = for {
    template <- alphaNumStr
  } yield LocationTemplate(template)

  def genLocation: Gen[Location] = for {
    host <- alphaNumStr
    port <- arbitrary[Int]
    datacenter <- alphaNumStr
    intent <- arbitrary[LocationIntent]
    templates <- arbitrary[List[LocationTemplate]]
  } yield Location(
    host = host,
    port = port,
    datacenter = datacenter,
    intent = intent,
    templates = templates)

  def genFlaskID: Gen[FlaskID] =
    for {
      id <- alphaNumStr
    } yield FlaskID(id)

  def genFlask: Gen[Flask] =
    for {
      flaskID <- arbitrary[FlaskID]
      location <- arbitrary[Location]
    } yield Flask(flaskID, location)

  def genDistribution: Gen[Distribution] = for {
    pairs <- arbitrary[List[(Flask, Set[Target])]]
  } yield ==>>(pairs: _*)

  val flask01 = Flask(FlaskID("flask01"),
    Location(
      host = "127.0.0.1",
      port = 5775,
      datacenter = "local",
      protocol = NetworkScheme.Http,
      intent = LocationIntent.Mirroring,
      templates = Seq.empty))

  val flask02 = Flask(FlaskID("flask02"),
    Location(
      host = "127.0.0.1",
      port = 6548,
      datacenter = "local",
      protocol = NetworkScheme.Http,
      intent = LocationIntent.Mirroring,
      templates = Seq.empty))

  val flask03 = Flask(FlaskID("flask03"),
    Location(
      host = "127.0.0.1",
      port = 4532,
      datacenter = "local",
      protocol = NetworkScheme.Http,
      intent = LocationIntent.Mirroring,
      templates = Seq.empty))
}
