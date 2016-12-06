package com.rest.service.demo.main;

import com.rest.service.demo.bo.User;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

/**
 * Created by liuzh on 16-3-29.
 */
public class RequestTest {

    public static void main(String s[]){

        for (int i = 0; i < 1000; i++){
            RequestThread rt = new RequestThread("usr" + i, "usr" + i);

            rt.start();
        }
    }


}

class RequestThread extends Thread{
    private final String name;
    private final String pwd;

    public RequestThread(String name, String pwd) {

        this.name = name;
        this.pwd = pwd;
    }

    @Override
    public void run() {
        User user = new User(name, pwd);

        ResteasyClient client = new ResteasyClientBuilder().build();

        ResteasyWebTarget target = client
                .target("http://127.0.0.1:9000/auth/login");

        Response response = target.request().post(
                Entity.entity(user, "application/json"));

        String resp = response.readEntity(String.class);
        String token = resp.split(",")[2].replaceAll("\"token\":", "").replaceAll("\"", "").replaceAll("}", "");

        System.out.println(token);

        target = client.target("http://127.0.0.1:9000/auth/hello");

        response = target.request().header("authorization", token).post(Entity.entity("", "application/json"));

        response.getStatus();

        response.close();
    }

}