package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.dao.PollRepository;
import no.hvl.dat250.h2020.group5.dao.UserRepository;
import no.hvl.dat250.h2020.group5.dao.VoteRepository;
import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.enums.AnswerType;
import no.hvl.dat250.h2020.group5.enums.PollVisibilityType;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.converters.StringToAnswerType;

import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PollService {

    final
    PollRepository pollRepository;

    final
    UserRepository userRepository;

    final
    VoteRepository voteRepository;

    StringToAnswerType stringToAnswerType = new StringToAnswerType();


    public PollService(PollRepository pollRepository, UserRepository userRepository, VoteRepository voteRepository) {
        this.pollRepository = pollRepository;
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
    }

    public Poll createPoll(Poll poll, Long userId) {
        Optional<User> foundUser = userRepository.findById(userId);
        if (foundUser.isPresent()) {
            User user = foundUser.get();
            poll.setPollOwner(user);
            return pollRepository.save(poll);
        } else {
            return null;
        }
    }

    public boolean deletePoll(Long pollId) {
        Optional<Poll> poll = pollRepository.findById(pollId);
        if (poll.isEmpty()) {
            return false;
        } else {
            pollRepository.delete(poll.get());
            return true;
        }
    }

    public List<Poll> getAllPublicPolls() {
        return pollRepository.findAllByVisibilityType(PollVisibilityType.PUBLIC);
    }

    public Poll getPoll(long pollId) {
        Optional<Poll> poll = pollRepository.findById(pollId);
        return poll.orElse(null);
    }

    public Boolean activatePoll(long pollId){
        Optional<Poll> poll = pollRepository.findById(pollId);
        if (poll.isEmpty()){
            return false;
        }

        poll.get().setStartTime(new Date());
        pollRepository.save(poll.get());
        return true;
    }


    public boolean getPollStatus(Long pollId) {
        Optional<Poll> poll = pollRepository.findById(pollId);
        if(poll.isEmpty()){
            return false;
        }

        Instant startTime = poll.get().getStartTime().toInstant();
        Instant startTimePlusDuration = startTime.plusSeconds(poll.get().getPollDuration());

        return !Instant.now().isAfter(startTimePlusDuration);
    }

    public int getNumberOfVotes(Long pollId, String avt) {
        Optional<Poll> foundPoll = pollRepository.findById(pollId);
        AnswerType answerType = stringToAnswerType.convert(avt);

        if (foundPoll.isPresent() && answerType != null) {
            List<Vote> votes = voteRepository.findByPollAndAnswer(foundPoll.get(), answerType);
            return votes.size();
        } else {
            return -1;
        }

    }

//    private void deleteVotes(String pollId){
//        em.getTransaction().begin();
//        Query q = em.createQuery("DELETE from Poll p WHERE p.id = :id");
//        q.setParameter("id", pollId);
//        q.executeUpdate();
//        em.getTransaction().commit();
//    }
}
