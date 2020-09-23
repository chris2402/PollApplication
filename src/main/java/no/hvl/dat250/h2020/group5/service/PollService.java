package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.dao.PollRepository;
import no.hvl.dat250.h2020.group5.dao.UserRepository;
import no.hvl.dat250.h2020.group5.dao.VoteRepository;
import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.enums.PollVisibilityType;
import no.hvl.dat250.h2020.group5.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

//TODO: Edit poll
@Service
public class PollService  {

     @Autowired
     PollRepository pollRepository;

     @Autowired
     UserRepository userRepository;

     @Autowired
     VoteRepository voteRepository;

     //TODO: REMOVE.
     private int i = 1;

    public Poll createPoll(Poll poll) {
//        Poll poll = new Poll();
//        Optional<User> pollOwner = userRepository.findById(userId);

//        PollVisibilityType visibilityType = isPublic ? PollVisibilityType.PUBLIC : PollVisibilityType.PRIVATE;

//        if(pollOwner.isPresent()){
//            poll.setName(name);
//            poll.setQuestion(question);
//            poll.setPollOwner(pollOwner.get());
//            poll.setPollDuration(duration);

        //TODO: Finn bruker ikkje opprett
        User user = new User();
        user.setUserName("oasfdikj");
        user.setPassword("ljkasdf");
        user.setId("sadf" + i);
        i++;
        poll.setPollOwner(user);


            return pollRepository.save(poll);
//        }
//        else{
//            return null;
//        }

    }

    //TODO: Check if user owns the poll or is an admin
    public boolean deletePoll(String pollId) {
        Optional<Poll> poll = pollRepository.findById(pollId);
        if(poll.isEmpty()){
            return false;
        }
        else{
            //TODO: DELETE VOTES
            pollRepository.delete(poll.get());
            return true;
        }
    }


    public List<Poll> getAllPublicPolls() {
        return pollRepository.findAllByVisibilityType(PollVisibilityType.PUBLIC);
    }


    public List<Poll> getOwnPolls(String userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(value -> pollRepository.findAllByPollOwner(value)).orElse(null);
    }


    public Poll getPoll(long pollId) {
        Optional<Poll> poll = pollRepository.findById(pollId);
        return poll.orElse(null);
    }



    //TODO: Check if user is an admin
    public boolean changePollStatus(String pollId, String userId, boolean status) {
        Optional<Poll> poll = pollRepository.findById(pollId);
        if(poll.isPresent() && poll.get().getPollOwner().getId().equals(userId)){
            Poll foundPoll = poll.get();
            foundPoll.setActive(status);
            pollRepository.save(foundPoll);
            return true;
        }
        else{
            return false;
        }
    }

//    private void deleteVotes(String pollId){
//        em.getTransaction().begin();
//        Query q = em.createQuery("DELETE from Poll p WHERE p.id = :id");
//        q.setParameter("id", pollId);
//        q.executeUpdate();
//        em.getTransaction().commit();
//    }
//
//    public void setup(){
//        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
//        em = factory.createEntityManager();
//    }
}
