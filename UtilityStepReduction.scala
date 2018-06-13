import Run.ResourceMap

class UtilityStepReduction extends Strategy {

  var powerSetGoals: List[List[Goal]] = _
  var unproposedSets: List[List[Goal]] = _

  def init(agent: Agent): Unit = {

    powerSetGoals = (for {
      len <- 1 to agent.goals.length
      combinations <- agent.goals combinations len
    } yield combinations).sortBy(l => l.foldLeft(0)((acc,i) => acc + i.utility)).toList

    unproposedSets = powerSetGoals

    agent.utility = agent.getNewUtilityState(agent.resources)

  }

  def createProposal(agent: Agent): Boolean = {

    if (unproposedSets.isEmpty) false

    else {

      val nextProposalSet = unproposedSets.last

      if (nextProposalSet.map(g => g.utility).sum <= agent.utility) false

      else {

        var totalRequired: ResourceMap = Map()
        var proposalDeltas: ResourceMap = Map()
        var proposalRequests: ResourceMap = Map()
        var proposalOfferings: ResourceMap = Map()

        unproposedSets = unproposedSets.dropRight(1)

        // Generates total required resources for current set of goals in 'nextProposalSet'
        for (g <- nextProposalSet; (r, v) <- g.resources)
          if (totalRequired contains r)
            totalRequired = totalRequired + (r -> (totalRequired(r) + v))
          else
            totalRequired = totalRequired + (r -> v)

        proposalDeltas = totalRequired
        for ((r, v) <- agent.resources)
          if (proposalDeltas contains r)
            proposalDeltas = proposalDeltas + (r -> (proposalDeltas(r) - v))

        proposalRequests = proposalDeltas.filter(g => g._2 > 0)
        proposalOfferings = proposalDeltas.filter(g => g._2 < 0).map(g => (g._1, g._2 * -1))

        new ProposalMessage(agent, agent.negotiationPartner, proposalRequests, proposalOfferings)

        true
      }

    }

  }

  def evaluateProposal(agent: Agent, proposal: ProposalMessage): Unit = {

    for ((r,v) <- proposal.requesting)
      if (agent.resources(r) - v < 0) {
        new RejectMessage(agent, agent.negotiationPartner, s"Not enough of resource ${r.id}.")
        return
      }

    if (agent.getNewUtilityState(agent.getNewResourceState(proposal.requesting, proposal.offering)) >= agent.utility)
      new AcceptMessage(agent, agent.negotiationPartner, proposal)

    else
      new RejectMessage(agent, agent.negotiationPartner, s"Proposal would not improve agent's utility.")

  }

}
