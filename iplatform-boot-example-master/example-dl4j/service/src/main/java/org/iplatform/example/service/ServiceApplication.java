package org.iplatform.example.service;

import org.iplatform.example.service.examples.IExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

@SpringBootApplication
public class ServiceApplication {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceApplication.class);

    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        try {
            context = SpringApplication.run(ServiceApplication.class, args);
            Map<String, IExample> examples = context.getBeansOfType(IExample.class);
            Map<Integer, IExample> indexs = new HashMap<>();

            Scanner input = new Scanner(System.in);
            System.out.println("examples:");
            Iterator<String> it = examples.keySet().iterator();
            Integer index = 0;
            while (it.hasNext()) {
                String key = it.next();
                indexs.put(index, examples.get(key));
                System.out.println(String.format("%d. %s", index, key));
                index++;
            }
            System.out.println("please input:");
            String name = input.nextLine();
            Integer selectedIndex = Integer.parseInt(name);
            IExample example = indexs.get(selectedIndex);
            System.out.println(String.format("run %s",example.getClass().getSimpleName()));
            example.run();

            System.out.println("Press any key to exit");
            input.nextLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
