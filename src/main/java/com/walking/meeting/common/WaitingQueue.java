package com.walking.meeting.common;

public class WaitingQueue<T> {

    // 里面塞 MeetingDO+时间戳+等级
    public static WaitingQueue waitingQueue = new WaitingQueue();

    static class node<T> {
        T data;
        node next;
        public node() {}
        public node(T data) {
            this.data = data;
        }
    }

    /**
     * 相当于head 头
     */
    node front;

    /**
     * 相当于tail/end  尾巴
     */
    node rear;

    public void listQueue() {
        front=new node<T>();
        rear=front;
    }

    public int length() {
        int len = 0;
        node team = front;
        while (team != rear) {
            len++;
            team = team.next;
        }
        return len;
    }

    public boolean isempty() {
        return rear == front;
    }

    /**
     * 队尾插入
     * @param value
     */
    public void enQueue(T value) {
        node node = new node<T>(value);
        rear.next = node;
        rear = node;
    }

    /**
     * 队头出队
     * @return
     * @throws Exception
     */
    public T deQueue() throws Exception{
        if (isempty()){
            throw new Exception("队列为空");
        }
        else {
            T ele = (T) front.next.data;
            front.next = front.next.next;
            return ele;
        }
    }

    public String toString() {
        node team = front.next;
        StringBuilder stringBuilder = new StringBuilder();
        while (team != null) {
            stringBuilder.append(team.data);
            team = team.next;
        }
        return stringBuilder.toString();
    }

}
