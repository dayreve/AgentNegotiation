import Run.ResourceMap

class ProposalMessage(override val sender: Agent, override val recipient: Agent,
                      val requesting: ResourceMap, val offering: ResourceMap)
  extends Message(sender, recipient) {

  println(s"${sender.name} has sent a proposal to ${recipient.name}. Proposal detailed below.")

  for ((r,v) <- requesting)
    println(s"${sender.name} has requested $v of resource with id ${r.id}.")

  for ((r,v) <- offering)
    println(s"${sender.name} has offered $v of resource with id ${r.id}.")

  sender.sendMessage(this)
  recipient.receiveMessage(this)

}
