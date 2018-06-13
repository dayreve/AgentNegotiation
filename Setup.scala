import Run.ResourceMap

class Setup ( val minGoals: Int,
              val maxGoals: Int,
              val numberOfResources: Int,
              val maxResourceCost: Int,
              val maxResourceAllocation: Int,
              val maxUtility: Int ) {

  val rand = scala.util.Random

  val resourceList = (1 to numberOfResources).map(i => new Resource(i))

  val a1Resources = resourceList.map(r => r -> rand.nextInt(maxResourceAllocation + 1)).toMap
  val a2Resources = resourceList.map(r => r -> rand.nextInt(maxResourceAllocation + 1)).toMap

  def createGoalRequirements(): ResourceMap = {

    val index = rand.nextInt(resourceList.length)

    resourceList.map(r => if (resourceList.indexOf(r) == index)
                            r -> (rand.nextInt(maxResourceCost) + 1)
                          else
                            r -> rand.nextInt(maxResourceCost + 1)).toMap

  }

  var a1Goals = (0 to minGoals).map(i => new Goal(rand.nextInt(maxResourceAllocation + 1), createGoalRequirements(), false)).toList
  var a2Goals = (0 to minGoals).map(i => new Goal(rand.nextInt(maxResourceAllocation + 1), createGoalRequirements(), false)).toList

  for (i <- minGoals to maxGoals) {

    if (rand.nextBoolean())
      a1Goals = new Goal(rand.nextInt(maxResourceAllocation + 1), createGoalRequirements(), false) :: a1Goals

    if (rand.nextBoolean())
      a2Goals = new Goal(rand.nextInt(maxResourceAllocation + 1), createGoalRequirements(), false) :: a2Goals

  }

  val Agent1 = new Agent("AgentOne", a1Goals, a1Resources, new UtilityStepReduction)
  val Agent2 = new Agent("AgentTwo", a2Goals, a2Resources, new UtilityStepReduction)

  Agent1.negotiationPartner = Agent2
  Agent2.negotiationPartner = Agent1

  def getAgents: (Agent, Agent) = (Agent1, Agent2)
}
