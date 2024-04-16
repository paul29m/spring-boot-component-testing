package com.component.testing.demo.app.run;

import com.component.testing.demo.DemoApplication;
import com.component.testing.demo.app.config.MockGitClientComponent;
import com.component.testing.demo.app.config.AppRunContainerConfig;


import org.springframework.boot.SpringApplication;

import java.io.IOException;

public class RunApplicationWithTestcontainers {

    public static void main(String[] args) throws IOException {
        MockGitClientComponent.setupGitClientMock();
        args = new String[]{"--spring.profiles.active=local-container"};
        SpringApplication.from(DemoApplication::main).with(AppRunContainerConfig.class).run(args);
    }

}
