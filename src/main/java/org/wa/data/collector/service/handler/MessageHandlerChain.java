package org.wa.data.collector.service.handler;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class MessageHandlerChain {
    
    private final MessageHandler chain;

    public MessageHandlerChain(
            LoggingHandler loggingHandler,
            NullCheckHandler nullCheckHandler,
            ValidationHandler validationHandler,
            SuccessHandler successHandler,
            ErrorHandler errorHandler) {
        
        this.chain = loggingHandler;
        loggingHandler.setNext(nullCheckHandler)
                .setNext(validationHandler)
                .setNext(successHandler)
                .setNext(errorHandler);
    }

}
