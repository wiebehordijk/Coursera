import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BaseballElimination {

    private final static double EPSILON = 1E-14;
    private final String[] teamNames;
    private final int[] wins;
    private final int[] losses;
    private final int[] left;
    private final int[][] games;
    private final int numTeams;
    private final Map<String, Integer> teams;
    private final int teamWithMaxWins;
    private final int teamVertexOffset;
    private final int target;

    public BaseballElimination(String filename)                    // create a baseball division from given filename in format specified below
    {
        In in = new In(filename);
        numTeams = in.readInt();
        teamVertexOffset = numTeams * (numTeams - 1) / 2 + 1;
        target = teamVertexOffset + numTeams;
        in.readLine();

        teamNames = new String[numTeams];
        wins = new int[numTeams];
        losses = new int[numTeams];
        left = new int[numTeams];
        games = new int[numTeams][numTeams];
        teams = new HashMap<String, Integer>();
        int maxWinsSoFar = 0;

        for (int i = 0; i < numTeams; i++) {
            teamNames[i] = in.readString();
            teams.put(teamNames[i], i);
            wins[i] = in.readInt();
            if (wins[i] > wins[maxWinsSoFar])
                maxWinsSoFar = i;
            losses[i] = in.readInt();
            left[i] = in.readInt();

            for (int j = 0; j < numTeams; j++) {
                games[i][j] = in.readInt();
            }

            in.readLine();
        }

        teamWithMaxWins = maxWinsSoFar;
    }

    public int numberOfTeams()                        // number of teams
    {
        return numTeams;
    }

    public Iterable<String> teams()                                // all teams
    {
        return Arrays.asList(teamNames);
    }

    public int wins(String team)                      // number of wins for given team
    {
        return wins[teamNumber(team)];
    }

    public int losses(String team)                    // number of losses for given team
    {
        return losses[teamNumber(team)];
    }

    public int remaining(String team)                 // number of remaining games for given team
    {
        return left[teamNumber(team)];
    }

    public int against(String team1, String team2)    // number of remaining games between team1 and team2
    {
        return games[teamNumber(team1)][teamNumber(team2)];
    }

    public boolean isEliminated(String team)              // is given team eliminated?
    {
        if (triviallyEliminated(teamNumber(team)))
            return true;

        FlowNetwork fn = flowNetwork(teamNumber(team));
        FordFulkerson ff = new FordFulkerson(fn, 0, target);
        return sourceCapacityLeft(fn, ff);
    }

    private boolean sourceCapacityLeft(FlowNetwork fn, FordFulkerson ff) {
        double sourceCapacity = 0.0;
        for (FlowEdge e : fn.adj(0))
            sourceCapacity += e.capacity();

        return (ff.value() + EPSILON < sourceCapacity);
    }

    public Iterable<String> certificateOfElimination(String team)  // subset R of teams that eliminates given team; null if not eliminated
    {
        if (triviallyEliminated(teamNumber(team))) {
            return Collections.singletonList(teamNames[teamWithMaxWins]);
        }

        FlowNetwork fn = flowNetwork(teamNumber(team));
        FordFulkerson ff = new FordFulkerson(fn, 0, target);
        if (!sourceCapacityLeft(fn, ff))
            return null;

        Bag<String> mincut = new Bag<String>();

        int tn = teamNumber(team);
        for (int i = 0; i < numTeams; i++) {
            if ((i != tn) && ff.inCut(teamVertexOffset + i))
                mincut.add(teamNames[i]);
        }

        return mincut;
    }

    private boolean triviallyEliminated(int team) {
        return (wins[team] + left[team] < wins[teamWithMaxWins]);
    }

    private int teamNumber(String teamName) {
        if (!teams.containsKey(teamName))
            throw new IllegalArgumentException("Team " + teamName + " unknown");

        return teams.get(teamName);
    }


    private FlowNetwork flowNetwork(int team) {
        FlowNetwork fn = new FlowNetwork(numTeams * (numTeams - 1) / 2 + numTeams + 2);

        // Edges from s to game vertices to team vertices
        for (int i = 0; i < numTeams; i++) {
            if (i == team)
                continue;
            for (int j = i + 1; j < numTeams; j++) {
                if (j == team)
                    continue;
                int v = gameVertex(i, j);
                fn.addEdge(new FlowEdge(0, v, games[i][j]));
                fn.addEdge(new FlowEdge(v, teamVertexOffset + i, Double.POSITIVE_INFINITY));
                fn.addEdge(new FlowEdge(v, teamVertexOffset + j, Double.POSITIVE_INFINITY));
            }
        }

        // Edges from teams to target
        int maxWinsTeam = wins[team] + left[team];
        for (int i = 0; i < numTeams; i++) {
            if (i == team)
                continue;
            fn.addEdge(new FlowEdge(teamVertexOffset + i, target, maxWinsTeam - wins[i]));
        }

        return fn;
    }

    private int gameVertex(int team1, int team2) {
        int t1, t2;
        assert (team1 != team2);
        if (team1 < team2) {
            t1 = team1;
            t2 = team2;
        } else {
            t1 = team2;
            t2 = team1;
        }

        return (numTeams - 1) * t1 - t1 * (t1 + 1) / 2 + t2;
    }


    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            } else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
