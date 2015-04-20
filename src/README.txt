This code implements a modification to strategyB. The issue here is that we can't recurse the whole tree for minimax, and we can't store the entire tree in memory because it would require a very large amount of RAM on our computers. Currently, the two strategies are strategyA and strategyB, both of which do the same thing (analyze by normal minimax, and when either we run out of time, or a particular max depth level is reached, subtract an uncertainty penalty from the current move and percolate up), with strategyA choosing to fill up the entire time limit if possible by creating another context and redoing the search.

I chose to modify strategyB by a probabilistic search, which instead of just shooting an uncertainty penalty to the higher recursion levels, instead stops to play a particular number of games (I left it as a variable, total_plays, but currently it only plays 10 games because otherwise it would run out of time at the higher levels of the tree) completely randomly, and adds up the number of wins, loses and draws. The formula for the score is then:

    score = (numWin - numLose - numDraws) / total_plays;

I found that only taking into account numWin gave me very strange playing results, where the strategy would ignore obvious potential wins by the other player. Including numLose improved it a little, but what surprised me was that counting the draws as a "minus" actually improved the results. I was expecting that my formula should be (numWin - numLose) / total_plays, but this was clearly not the case.

I also found that the minDepth and maxDepth arguments to StrategyB were causing it to run out of time, and at first I saw that it was playing very badly and could easily be beaten by a vertical line of four by the other player - strategyB wouldn't even try to contest it. Then, I looked at these two parameters in Connect4Control, and set maxDepth to 10, and minDepth to 4. Of these two, minDepth had the bigger effect, since at a minDepth of 5, regardless of how low I set maxDepth, StrategyB kept running out of time, even with as few as 10 games played ahead. I think this is purely reflective of how "wide" the tree levels are as opposed to anything else. That said, once I set all of these parameters, these were the results for strategyB as yellow, over 10 games:

yellow wins: 10
red wins: 0

And these were the results for strategyB as red, over 15 games:

red wins : 15
yellow wins: 0

I found this very suspicious given the fact that this randomized algorithm is fairly dumb, but you can test it out for yourself. I will keep running tests and tallying up results in case I somehow find something more statistically significant.

I also note that this only worked if I had 

	 searchResult.setIsResultFinal(false);

For the context involving the probabilistic search. If I set this to true, I run into issues, so keeping the uncertainty penalty at 0.1 seems to work well.

