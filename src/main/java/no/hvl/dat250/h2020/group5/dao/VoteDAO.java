package no.hvl.dat250.h2020.group5.dao;

public interface VoteDAO {
    boolean voteA();
    boolean voteB();

    boolean multipleVotesA(int votes);
    boolean multipleVotesB(int votes);
}
