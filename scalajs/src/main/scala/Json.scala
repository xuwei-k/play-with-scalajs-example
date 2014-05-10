package scalajs.json

import scala.scalajs.js
import scala.scalajs.js.{Dynamic => JsDynamic}
import argonaut._

object Scalajs2Json {
  def apply(json: js.Dynamic): Json = json match {
    case s: js.String =>
      Json.jString(s)
    case n: js.Number =>
      Json.jNumber(n)
    case a: js.Array[JsDynamic] =>
      Json.jArray(a.map((j: JsDynamic) => apply(j)).toList)
    case b: js.Boolean =>
      if(b) Json.jTrue else Json.jFalse
    case o: js.Object =>
      Json.jObjectAssocList(
        js.Object.keys(o).toList.map{ key =>
          (key: String) -> apply(o.selectDynamic(key))
        }(collection.breakOut)
      )
  }
}
