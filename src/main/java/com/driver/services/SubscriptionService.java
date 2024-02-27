package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay

        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setStartSubscriptionDate(new Date());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());

        int totalAmount = 0;

        if(subscription.getSubscriptionType().equals(SubscriptionType.BASIC)){
            totalAmount =  500 + 200* subscription.getNoOfScreensSubscribed();
        }
        else if(subscription.getSubscriptionType().equals(SubscriptionType.PRO)){
            totalAmount =  800 + 250*subscription.getNoOfScreensSubscribed();
        }
        else
            totalAmount =  1000 + 300*subscription.getNoOfScreensSubscribed();

        subscription.setTotalAmountPaid(totalAmount);

        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();

        subscription.setUser(user);

        user.setSubscription(subscription);

        userRepository.save(user);

        //Yahan prr bhi userRepo get kaafi baar get waala call ho jaye toh uska dhyaan dena hai
        //yahan prr apne ko subscription bhi mock kr deni chahiye incase somebody saves subscription first and sets it into user and then saves the user
        //Also we need to assert that for a subscription a user is set or not....

        return totalAmount;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        User user = userRepository.findById(userId).get();

        Subscription subscription = user.getSubscription();

        if(subscription.getSubscriptionType().equals(SubscriptionType.ELITE)){
            throw new Exception("Already the best Subscription");
        }
        if(subscription.getSubscriptionType().equals(SubscriptionType.BASIC)){
            subscription.setSubscriptionType(SubscriptionType.PRO);
            subscription.setTotalAmountPaid(subscription.getNoOfScreensSubscribed()*250 + 800);
            subscriptionRepository.save(subscription);
            return 300 + 50*subscription.getNoOfScreensSubscribed();
        }
        subscription.setSubscriptionType(SubscriptionType.ELITE);
        subscription.setTotalAmountPaid(subscription.getNoOfScreensSubscribed()*300 + 1000);
        subscriptionRepository.save(subscription);

        //Yahan prr check krna hai ki vo bnda userRepo ko save naaa krre.....

        return 200 + 100*subscription.getNoOfScreensSubscribed();
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        List<Subscription> subscriptionList = subscriptionRepository.findAll();

        int totalAmount = 0;

        for(Subscription subscription: subscriptionList){
            totalAmount = totalAmount + subscription.getTotalAmountPaid();
        }
        return totalAmount;

    }

}