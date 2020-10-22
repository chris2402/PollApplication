# PollApplication  

[![Build Status](https://travis-ci.com/chris2402/PollApplication.svg?token=AsNzGpopnC9RcQp5F1w4&branch=dev)](https://travis-ci.com/chris2402/PollApplication)



#### Plugins   
Lombok plugin need to be install in order to run the application.


#### Design    
Initially in task A we wanted VoteDevice to be associated with a user, such that each user has an own 
voting device. An example of this use could be to have a collection of people wanting to 
               vote at the same time, because then they need a votingDevice each. After some discussion we figured out that it would be easier to implement the Device as just a Voter, 
not having to have a relationship with a user. An example of this use would be in a classroom where the students go and vote 
on the votingDevice after each other.    

Using this latter approach we could let a Device send in the number of yes and nos, count them and create
a new vote for each yes and no. The votes are then persisted using `voteRepository.saveAll(votes)`.

#### Postman
Before running the postman collections be sure to fill the databse. As of now check the [branch "throwaway/databaseInitialize"](https://github.com/chris2402/PollApplication/tree/throwaway/initializeData) and make sure that the @Autogenerated on ID in voter and poll is commented out when running with the databaseIntialize and also make sure that the @PostConstruct in databaseInitalize.java is not commented out. Restart the application and remove @Postconstruct in databaseInitalize and re-add the autogenerated ID for poll and voter. **This wil be fixed at a later point** as there were some problems using a data.sql file.

#### Branch naming convention

##### 1. Bruk prefixes
* feat/
* hotfix/
* fix/
* throwaway/  
etc..

##### 2. Prøv å ha "short and sweet" navn

* ~~hotfix/bug-when-user-tries-to-use-æøå-in-poll-title-the-server-dies~~

* hotfix/æøå-crash


##### 3. Ved merge skriv gjerne en liten "changelog" (spesielt på større merges)

Eksempel  

---

**Added**
* Route guarding.  
  1. Private polls
  2. Admin page
  3. My polls page

**Changed**
* Now always warns user before they can navigate back
* History mode instead of hash mode in router.

---

**Slett branch etter merge.**


**Om merges også blir lagt labels på så er det litt enklere å lete etter konkrete merges senere ;)**


#### Deployment

We use Heroku for deployment. 

**How to deploy**   
Download [Heroku CLI](https://devcenter.heroku.com/articles/heroku-cli) if you dont have it.    

``heroku login``   
` heroku git:remote -a pollapplication-dat250-group5`   
``git push heroku master`` or `git push heroku <your-branch>:master` if on another branch then master.  

The deployment is [here](https://pollapplication-group5.herokuapp.com)!


