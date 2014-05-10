package shared

import argonaut.CodecJson

final case class Message(
  kind: String,
  user: String,
  message: String,
  members: List[String]
)

object Message {

  implicit val codec: CodecJson[Message] =
    CodecJson.casecodec4(apply, unapply)(
      "kind", "user", "message", "members"
    )

}
