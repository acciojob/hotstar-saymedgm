package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){
        user = userRepository.save(user);
        return user.getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository

        User user = userRepository.findById(userId).get();
        Subscription subscription = user.getSubscription();
        SubscriptionType subscriptionType = subscription.getSubscriptionType();

        List<WebSeries> webSeriesList = webSeriesRepository.findAll();

        //Once we have got all the webSeriesList
        int count = 0;

        for(WebSeries webSeries : webSeriesList){

            if(isPossible(subscriptionType,webSeries.getSubscriptionType())){

                if(user.getAge()>=webSeries.getAgeLimit()){
                    count++;
                }
            }
        }
        return count;
    }

    private boolean isPossible(SubscriptionType currentSubscription,SubscriptionType webSeriesSubscription){

        if(currentSubscription.equals(SubscriptionType.ELITE)){
            return true;
        }
        else if(currentSubscription.equals(SubscriptionType.PRO) && !webSeriesSubscription.equals(SubscriptionType.ELITE)){
            return true;
        }
        if(webSeriesSubscription.equals(SubscriptionType.BASIC))
            return true;
        return false;
    }


}