package scalajs.json

import scala.scalajs.js
import scala.scalajs.js.{Dynamic => JsDynamic}

object Scalajs2Json {
  def apply(json: js.Dynamic): Json = json match {
    case s: js.String =>
      JString(s)
    case n: js.Number =>
      JNumber(n)
    case a: js.Array[JsDynamic] =>
      JArray(a.map((j: JsDynamic) => apply(j)).toList)
    case b: js.Boolean =>
      if(b) Json.True else Json.False
    case o: js.Object =>
      JObject(JsonObject(
        js.Object.keys(o).toList.map{ key =>
          (key: String) -> apply(o.selectDynamic(key))
        }(collection.breakOut)
      ))
  }
}
