class RejectMessage (override val sender: Agent, override val recipient: Agent, val context: String)
  extends Message(sender, recipient) {

  println(s"${sender.name} has rejected the proposal from ${recipient.name}.")
  println(s"Context for rejection: $context")

  sender.sendMessage(this)
  recipient.receiveMessage(this)

}
