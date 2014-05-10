package scalajs.json

object CaseClassCodec {

  def _2[A1, A2, Z](applyF: (A1, A2) => Z, unapplyF: Z => Option[(A1, A2)])(key1: String, key2: String)(implicit A1: Codec[A1], A2: Codec[A2]): Codec[Z] =
    Codec{
      case JObject(obj) =>
        for{
          a1 <- obj.value.get(key1).map(A1.decode).toRight(key1 + " not found").joinRight.right
          a2 <- obj.value.get(key2).map(A2.decode).toRight(key2 + " not found").joinRight.right
        } yield applyF(a1, a2)
      case other =>
        Left(other + " is not object")
    }{
      obj =>
        val tuple = unapplyF(obj).get
        JObject(JsonObject(Map(
          key1 -> A1.encode(tuple._1),
          key2 -> A2.encode(tuple._2)
        )))
    }

  def _3[A1, A2, A3, Z](applyF: (A1, A2, A3) => Z, unapplyF: Z => Option[(A1, A2, A3)])(key1: String, key2: String, key3: String)(implicit A1: Codec[A1], A2: Codec[A2], A3: Codec[A3]): Codec[Z] =
    Codec{
      case JObject(obj) =>
        for{
          a1 <- obj.value.get(key1).map(A1.decode).toRight(key1 + " not found").joinRight.right
          a2 <- obj.value.get(key2).map(A2.decode).toRight(key2 + " not found").joinRight.right
          a3 <- obj.value.get(key3).map(A3.decode).toRight(key3 + " not found").joinRight.right
        } yield applyF(a1, a2, a3)
      case other =>
        Left(other + " is not object")
    }{
      obj =>
        val tuple = unapplyF(obj).get
        JObject(JsonObject(Map(
          key1 -> A1.encode(tuple._1),
          key2 -> A2.encode(tuple._2),
          key3 -> A3.encode(tuple._3)
        )))
    }

  def _4[A1, A2, A3, A4, Z](applyF: (A1, A2, A3, A4) => Z, unapplyF: Z => Option[(A1, A2, A3, A4)])(key1: String, key2: String, key3: String, key4: String)(implicit A1: Codec[A1], A2: Codec[A2], A3: Codec[A3], A4: Codec[A4]): Codec[Z] =
    Codec{
      case JObject(obj) =>
        for{
          a1 <- obj.value.get(key1).map(A1.decode).toRight(key1 + " not found").joinRight.right
          a2 <- obj.value.get(key2).map(A2.decode).toRight(key2 + " not found").joinRight.right
          a3 <- obj.value.get(key3).map(A3.decode).toRight(key3 + " not found").joinRight.right
          a4 <- obj.value.get(key4).map(A4.decode).toRight(key4 + " not found").joinRight.right
        } yield applyF(a1, a2, a3, a4)
      case other =>
        Left(other + " is not object")
    }{
      obj =>
        val tuple = unapplyF(obj).get
        JObject(JsonObject(Map(
          key1 -> A1.encode(tuple._1),
          key2 -> A2.encode(tuple._2),
          key3 -> A3.encode(tuple._3),
          key4 -> A4.encode(tuple._4)
        )))
    }

}
