// com/cardgame/config/GameConfig.java
package com.cardgame.config;

import com.cardgame.card.Deck;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Robust Deck bean creator that tolerates different Deck implementations.
 * It tries, in order:
 *  1) new Deck()
 *  2) new Deck(int numDecks)            // uses 6
 *  3) new Deck(int numDecks, boolean)   // (6, true)
 *  4) Deck.newShoe(int) / Deck.standard(int) / Deck.create(int) static factories (if exist)
 *  5) Deck.of() / Deck.of(int) / Deck.defaultShoe()
 */
@Configuration
public class GameConfig {

    @Bean
    public Deck shoe() {
        final String cls = "com.cardgame.card.Deck";
        final int NUM = 6; // change if you want different shoe size

        try {
            Class<?> deckClass = Class.forName(cls);

            // 1) no-arg
            try {
                Constructor<?> c = deckClass.getDeclaredConstructor();
                c.setAccessible(true);
                return (Deck) c.newInstance();
            } catch (NoSuchMethodException ignored) {}

            // 2) (int)
            try {
                Constructor<?> c = deckClass.getDeclaredConstructor(int.class);
                c.setAccessible(true);
                return (Deck) c.newInstance(NUM);
            } catch (NoSuchMethodException ignored) {}

            // 3) (int, boolean)
            try {
                Constructor<?> c = deckClass.getDeclaredConstructor(int.class, boolean.class);
                c.setAccessible(true);
                return (Deck) c.newInstance(NUM, true);
            } catch (NoSuchMethodException ignored) {}

            // 4) static factories
            String[] factories = {"newShoe", "standard", "create"};
            for (String f : factories) {
                for (Class<?>[] sig : new Class<?>[][]{
                        {int.class},
                        {}
                }) {
                    try {
                        Method m = deckClass.getDeclaredMethod(f, sig);
                        m.setAccessible(true);
                        Object inst = sig.length == 1 ? m.invoke(null, NUM) : m.invoke(null);
                        if (inst instanceof Deck d) return d;
                    } catch (NoSuchMethodException ignored) {}
                }
            }

            // 5) "of"/"defaultShoe"
            try {
                Method m = deckClass.getDeclaredMethod("of", int.class);
                m.setAccessible(true);
                Object inst = m.invoke(null, NUM);
                if (inst instanceof Deck d) return d;
            } catch (NoSuchMethodException ignored) {}

            try {
                Method m = deckClass.getDeclaredMethod("of");
                m.setAccessible(true);
                Object inst = m.invoke(null);
                if (inst instanceof Deck d) return d;
            } catch (NoSuchMethodException ignored) {}

            try {
                Method m = deckClass.getDeclaredMethod("defaultShoe");
                m.setAccessible(true);
                Object inst = m.invoke(null);
                if (inst instanceof Deck d) return d;
            } catch (NoSuchMethodException ignored) {}

            throw new IllegalStateException("Cannot instantiate Deck: no known constructors/factories matched.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Deck bean via reflection: " + e, e);
        }
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }
}