# Rubbish Collection Simulation using a genetic algorithm

This project proposes a state-based rubbish collection simulation. In this, the collector agents move around the environment to memorise rubbish locations (MAP STATE). They then use this memory, along with a genetic algorithm to plot the fastest route through the environment to collect all the rubbish (CALCULATION STATE). Finally, the agents action the planned route, collecting rubbish (ACTION STATE) - similar to the Travelling Salesman Problem

The following questions regarding sensitivity analysis are posed:
- How do different heuristics and hyperparameters compare when looking to clean up the most rubbish in a fixed time period? (e.g. hill climbing, crossover, mutation, depth of search, intensity of mutation)
- How does the number of agents affect the speed at which rubbish is picked up, or the number of bits of rubbish that are picked up in total?
- How does the number of bits of rubbish in the environment affect the agent's ability to map out rubbish, or their ability to find an optimal route to collect all the rubbish?
