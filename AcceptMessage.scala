class AcceptMessage (override val sender: Agent, override val recipient: Agent, val proposal: ProposalMessage)
  extends Message(sender, recipient) {

  println(s"${sender.name} has accepted the proposal from ${recipient.name}.")

  sender.tradeResources(proposal.offering, proposal.requesting)
  recipient.tradeResources(proposal.requesting, proposal.offering)

  sender.utility = sender.getNewUtilityState(sender.resources)
  recipient.utility = recipient.getNewUtilityState(recipient.resources)

  sender.sendMessage(this)
  recipient.receiveMessage(this)

}
