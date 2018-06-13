class EndMessage (override val sender: Agent, override val recipient: Agent)
  extends Message(sender, recipient) {

  sender.sendMessage(this)
  recipient.receiveMessage(this)

}
