package shared

import scalajs.json.{Codec, CaseClassCodec}
import scalajs.json.DefaultCodec._

final case class Message(
  kind: String,
  user: String,
  message: String,
  members: List[String]
)

object Message {

  implicit val codec: Codec[Message] =
    CaseClassCodec._4(apply, unapply)(
      "kind", "user", "message", "members"
    )

}
