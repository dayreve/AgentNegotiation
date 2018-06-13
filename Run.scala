object Run {

  type ResourceMap = Map[Resource, Int]

  def main(args: Array[String]): Unit = {

    val setup = new Setup(5, 10, 5, 20, 200, 10)

    val Agent1 = setup.getAgents._1
    val Agent2 = setup.getAgents._2

    println(s"Agent name: ${Agent1.name}; starting utility: ${Agent1.utility}")
    println(s"Agent name: ${Agent2.name}; starting utility: ${Agent2.utility}")
    println("Negotiation started.")

    if (!Agent1.strategy.createProposal(Agent1))
      Agent2.strategy.createProposal(Agent2)

  }

}
