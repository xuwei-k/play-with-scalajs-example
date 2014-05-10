package scalajs.json

import Decoder.{DecodeError, DecodeResult}

trait Decoder[A] {
  def decode(json: Json): DecodeResult[A]

  final def decodeOrError(json: Json): A =
    decode(json) match {
      case Right(value) => value
      case Left(error) => sys.error(error)
    }
}

object Decoder {
  type DecodeError = String
  type DecodeResult[A] = Either[DecodeError, A]
}


trait Encoder[A] {
  def encode(obj: A): Json
}

object Encoder {

}

trait Codec[A] extends Decoder[A] with Encoder[A]

object Codec {
  def apply[A](decoder: Json => DecodeResult[A])(encoder: A => Json): Codec[A] =
    new Codec[A] {
      def encode(obj: A) = encoder(obj)
      def decode(json: Json) = decoder(json)
    }
}

sealed abstract class LowPriorityCodec {
}

object DefaultCodec extends LowPriorityCodec {

  implicit val int: Codec[Int] = Codec{
    case JNumber(value) => Right(value.toInt)
    case other => Left(other + " is not number")
  }{
    value => JNumber(value)
  }

  implicit val string: Codec[String] = Codec{
    case JString(value) => Right(value)
    case other => Left(other + " is not string")
  }{
    value => JString(value)
  }

  implicit val bool: Codec[Boolean] = Codec{
    case JBool(value) => Right(value)
    case other => Left(other + " is not boolean")
  }{
    value => JBool(value)
  }

  implicit def list[A](implicit A: Codec[A]): Codec[List[A]] = Codec {
    case JArray(array) =>
      @annotation.tailrec
      def loop(array0: List[Json], result: List[A]): DecodeResult[List[A]] = array0 match {
        case Nil =>
          Right(result.reverse)
        case h :: t =>
          A.decode(h) match {
            case Right(r) =>
              loop(t, r :: result)
            case Left(error) =>
              Left(error)
          }
      }
      loop(array, Nil)
    case other => Left(other + " is not list")
  }{
    list => JArray(list.map(A.encode))
  }
}

