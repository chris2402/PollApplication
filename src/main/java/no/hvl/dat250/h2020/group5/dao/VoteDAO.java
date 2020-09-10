package no.hvl.dat250.h2020.group5.dao;

public interface VoteDAO {
    boolean vote(String pollId, String userId, String vote);
    boolean changeVote(String pollId, String userId, String vote);
    boolean deleteVote(String pollId, String userId);
}
