package com.nikhil.sonicmuse;

import org.apache.tika.Tika;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SonicMuseApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(SonicMuseApplication.class, args);
    }

    @Bean
    public Tika tika()
    {
        return new Tika();
    }
}
