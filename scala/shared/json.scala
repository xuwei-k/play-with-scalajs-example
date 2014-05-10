package scalajs.json

object Json {
  val True = JBool(true)
  val False = JBool(false)
}
sealed abstract class Json
final case object JNull extends Json
final case class JBool private[json](value: Boolean) extends Json
final case class JNumber(value: Double) extends Json
final case class JString(value: String) extends Json
final case class JArray(value: List[Json]) extends Json
final case class JObject(value: JsonObject) extends Json

final case class JsonObject(value: Map[String, Json])

