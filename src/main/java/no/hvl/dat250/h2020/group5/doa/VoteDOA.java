package no.hvl.dat250.h2020.group5.doa;

public interface VoteDOA {
    boolean voteA();
    boolean voteB();

    boolean multipleVotesA(int votes);
    boolean multipleVotesB(int votes);
}
