trait Strategy {

  var powerSetGoals: List[List[Goal]]

  def init(agent: Agent): Unit

  def createProposal(agent: Agent): Boolean

  def evaluateProposal(agent: Agent, proposal: ProposalMessage): Unit

}
