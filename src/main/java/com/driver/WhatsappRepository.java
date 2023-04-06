package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashMap<String,User> userDb;


    static private int customGroupCount;
    static private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userDb = new HashMap<String,User>();


    }

    public String createUser(String name, String mobile) throws Exception
    {
        if(userDb.containsKey(mobile))
            throw new Exception("User already exists");
        else
        {
            User user = new User(name,mobile);
            userDb.put(mobile,user);
            return "SUCCESS";
        }
    }

    public Group createGroup(List<User> users)
    {
        Group group = null;
        if(users.size()==2)
        {
            User user1 = users.get(0);
            User user2 = users.get(1);
            group = new Group(user2.getName(),2);
            groupUserMap.put(group,users);
            adminMap.put(group,user1);
        }
        else if(users.size()>2)
        {
            customGroupCount++;
            String groupName = "Group "+customGroupCount;
            User user1 = users.get(0);
            group = new Group(groupName,users.size());
            groupUserMap.put(group,users);
            adminMap.put(group,user1);
        }
        return group;
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception{
        if(groupUserMap.containsKey(group))
            throw new Exception("Group does not exist");
        if(isUserInGroup(group,sender)==false)
            throw new Exception("You are not allowed to send message");
        senderMap.put(message,sender);
        if(groupMessageMap.containsKey(group))
        {
            List<Message> messageList = groupMessageMap.get(group);
            messageList.add(message);
            groupMessageMap.put(group,messageList);
        }
        else
        {
            List<Message> messageList = new ArrayList<>();
            messageList.add(message);
            groupMessageMap.put(group,messageList);
        }
        return groupMessageMap.get(group).size();

    }

    public boolean isUserInGroup(Group group, User user)
    {

        List<User> userList = groupUserMap.get(group);
        for(User user1: userList)
        {
            if(user1.equals(user))
            {
                return true;
            }
        }
        return false;
    }

    public int createMessage(String content){
        messageId++;
        Message message = new Message(messageId,content,new Date());
        return messageId;
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception{
        if(!groupUserMap.containsKey(group))
            throw new Exception("Group does not exist");
        if(!adminMap.get(group).equals(approver))
            throw new Exception("Approver does not have rights");
        if(!isUserInGroup(group,user))
            throw new Exception("User is not a participant");
        adminMap.put(group,user);
        return "SUCCESS";
    }

}
