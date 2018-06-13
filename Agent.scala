import scala.collection.mutable.ListBuffer
import Run.ResourceMap

class Agent (val name: String, val goals: List[Goal], var resources: ResourceMap, val strategy: Strategy) {

  var utility = 0
  var receivedMessages = new ListBuffer[Message]()
  var sentMessages = new ListBuffer[Message]()
  var negotiationPartner: Agent = _
  var withdrawn = false

  strategy.init(this)

  def sendMessage(msg: Message): Unit = msg match {

    case a: AcceptMessage =>
      sentMessages += msg
      if (!strategy.createProposal(this)) endNegotiation()

    case r: RejectMessage =>
      sentMessages += msg
      if (!strategy.createProposal(this)) endNegotiation()

    case _ => sentMessages += msg

  }

  def receiveMessage(msg: Message): Unit = msg match {

    case p: ProposalMessage => receivedMessages += msg; strategy.evaluateProposal(this, p)
    case e: EndMessage => receivedMessages += msg; if (!strategy.createProposal(this)) endNegotiation()
    case _ => receivedMessages += msg

  }

  def endNegotiation(): Unit = {

    withdrawn = true

    println(s"$name has finished sending proposals.")

    if (negotiationPartner.withdrawn) {
      println("Both agents have finished proposing. Negotiation ended.")
      println(
        s"""
        |Final negotiation state:
        |$name finished with $utility utility.
        |${negotiationPartner.name} finished with ${negotiationPartner.utility} utility.
         """.stripMargin)

      sys exit 0
    }

    else new EndMessage(this, negotiationPartner)

  }

  def tradeResources(in: ResourceMap, out: ResourceMap): Unit = {

    for ((r,v) <- in)
      resources = resources + (r -> (resources(r) + v))

    for ((r,v) <- out)
      resources = resources + (r -> (resources(r) - v))

  }

  def getNewUtilityState(resources: ResourceMap): Int = {

    val potential = strategy.powerSetGoals.filter(gs => allocateResourcesToGoalSet(resources, gs)).map(gs => gs.map(g => g.utility).sum)

    potential match {
      case Nil => 0
      case _ => potential.max
    }

  }

  def allocateResourcesToGoalSet(_resources: ResourceMap, goalSet: List[Goal]): Boolean = {

    var possibleSet = true

    var rs = _resources

    for (g <- goalSet; (r,v) <- g.resources)
      if (rs(r) - v > -1)
        rs = rs + (r -> (rs(r) - v))
      else
        possibleSet = false

    possibleSet

  }

  def getNewResourceState(requested: ResourceMap, offered: ResourceMap): ResourceMap = {

    var newResourceState = resources

    for ((r,v) <- requested)
      newResourceState = newResourceState + (r -> (newResourceState(r) - v))

    for ((r,v) <- offered)
      newResourceState = newResourceState + (r -> (newResourceState(r) + v))

    newResourceState

  }

}
