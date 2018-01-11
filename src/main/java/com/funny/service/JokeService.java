package com.funny.service;

import com.funny.entity.Image;
import com.funny.entity.Joke;
import com.funny.entity.Tag;
import com.funny.utils.Utils;
import org.springframework.stereotype.Component;

@Component
public class JokeService extends BaseService<Joke> {


    @Override
    public Joke save(Joke joke) throws Exception {
        // 如果一个list的一个元素为null，则会报错，需要将其移除
        for (int i = 0; joke.getTags() != null && i < joke.getTags().size(); i++) {
            if (joke.getTags().get(i).getId() == null) {
                joke.getTags().remove(joke.getTags().get(i));
                i--;
            }
        }

        return super.save(joke);
    }
}
