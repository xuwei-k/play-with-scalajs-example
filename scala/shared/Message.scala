package shared

final case class Message(
  kind: String,
  user: String,
  message: String,
  members: List[String]
)
