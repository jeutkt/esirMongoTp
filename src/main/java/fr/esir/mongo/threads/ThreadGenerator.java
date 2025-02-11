package fr.esir.mongo.threads;

import fr.esir.mongo.text.TextGenerator;
import fr.esir.mongo.users.User;
import fr.esir.mongo.users.UserGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

/**
 *
 * @author lboutros
 */
@Component
@AllArgsConstructor
@Slf4j
public class ThreadGenerator implements Processor {

  private final static Random RANDOM = new Random(System.currentTimeMillis());

  // TODO initialize/read values in mongo
  // This is a dummy example, you should NEVER do that for a production app
  private final AtomicInteger id = new AtomicInteger(0);
  private final ConcurrentHashMap<String, Thread> knownThreads = new ConcurrentHashMap<>();

  private final TextGenerator textGenerator;

  private final UserGenerator userGenerator;

  @Override
  public void process(Exchange exchange) throws Exception {
    exchange.getIn().setBody(generateThread());
  }

  // TODO manage post/thread/user relashionship
  private Thread generateThread() {
    User randomKnownUser = userGenerator.getRandomKnownUser();
    List<String> tags = this.getTags();
    if (randomKnownUser != null) {
      String idString = Long.toString(id.getAndIncrement());
      Thread newThread = Thread.builder()
              ._id(idString)
              .title(textGenerator.generateText(1))
              .tags(tags)
              .build();

      knownThreads.put(idString, newThread);

      return newThread;
    } else {
      log.warn("Cannot create thread, no user created yet.");
      return null;
    }
  }

  public Thread getRandomThread() {
    if (knownThreads.isEmpty()) {
      return null;
    } else {
      return knownThreads.get(Long.toString(RANDOM.nextInt(id.get())));
    }
  }

  private List<String> getTags(){
    List<String> tags=new ArrayList<String>();
    String[] tagArray ={"golf","science","computer Science","school", "devops","programming"};

    int tmp = (int)(Math.random()*tagArray.length);
    for(int i=0;i<tmp;i++) {
      int tmp2 = (int)(Math.random()*tagArray.length);
      String tmp3 = tagArray[tmp2];
        if(!tags.contains(tmp3)) {
          tags.add(tmp3);
        }

    }
    return tags;
  }
}
