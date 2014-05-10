// https://github.com/playframework/playframework/blob/2.3.0-RC1/samples/scala/websocket-chat/app/views/chatRoom.scala.js

package example

import scala.scalajs.js
import org.scalajs.dom.{document, WebSocket, KeyboardEvent, MessageEvent}
import scala.scalajs.js.JSON
import js.Dynamic.{ global => g }
import js.annotation.JSExport
import org.scalajs.jquery.{jQuery => $}

@JSExport
object ChatRoom {

  @JSExport
  def chat(socketURL: String, username: String): js.Any = {
    val chatSocket = new WebSocket(socketURL)

    val sendMessage: () => js.Any = { () =>
      val obj = js.Dictionary(("text", $("#talk").`val`()))
      val message = JSON.stringify(obj)
      println(message)
      chatSocket.send(message)
      $("#talk").`val`("")
    }

    val receiveEvent: MessageEvent => js.Any = { event =>
      val data0 = JSON.parse(event.data.asInstanceOf[String])
      if(data0.error.asInstanceOf[js.Boolean]) {
         chatSocket.close()
         $("#onError span").text(data0.error.asInstanceOf[js.String])
         $("#onError").show()
      } else {
         $("#onChat").show()
      }

      val data = shared.Message(
        kind = data0.kind.asInstanceOf[String],
        user = data0.user.asInstanceOf[String],
        message = data0.message.asInstanceOf[String],
        members = data0.members.asInstanceOf[js.Array[String]].toList
      )

      // Create the message element
      val el = $("""<div class="message"><span></span><p></p></div>""")
      $("span", el).text(data.user)
      $("p", el).text(data.message)
      $(el).addClass(data.kind)
      if(data.user == username) $(el).addClass("me")
      $("#messages").append(el)

      // Update the members list
      $("#members").html("")
      data.members.foreach{ member =>
        val li = document.createElement("li")
        li.textContent = member
        $("#members").append(li)
      }
    }

    val handleReturnKey = (e: KeyboardEvent) => {
      val enterKey: js.Number = 13
      if(e.keyCode == enterKey) {
        e.preventDefault()
        sendMessage()
      }
    }

    $("#talk").keypress(handleReturnKey)

    chatSocket.onmessage = receiveEvent
  }
}

