// package com.edu.openai.test;
//
// import com.edu.openai.common.Constants;
// import com.edu.openai.executor.model.chatgpt.valobj.ChatGPTCompletionRequest;
// import com.edu.openai.executor.model.chatgpt.valobj.ChatGPTCompletionResponse;
// import com.edu.openai.executor.parameter.Message;
// import com.edu.openai.session.Configuration;
// import com.edu.openai.session.OpenAiSession;
// import com.edu.openai.session.OpenAiSessionFactory;
// import com.edu.openai.session.defaults.DefaultOpenAiSessionFactory;
//
//
// import java.util.ArrayList;
// import java.util.Scanner;
// import java.util.concurrent.CompletableFuture;
// import java.util.concurrent.ExecutionException;
//
// //开始对话模式
// public class ClientTest {
//     public static void main(String[] args) throws ExecutionException, InterruptedException {
//         Configuration configuration = new Configuration();
//         configuration.setApiHost("https://api.openai-proxy.com/");
//         configuration.setApiKey("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");
//         configuration.setAuthToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ4ZmciLCJleHAiOjE2ODMyODE2NzEsImlhdCI6MTY4MzI3ODA3MSwianRpIjoiMWUzZTkwYjYtY2UyNy00NzNlLTk5ZTYtYWQzMWU1MGVkNWE4IiwidXNlcm5hbWUiOiJ4ZmcifQ.YgQRJ2U5-9uydtd6Wbkg2YatsoX-y8mS_OJ3FdNRaX0");
//         // 2. 会话工厂
//         OpenAiSessionFactory factory = new DefaultOpenAiSessionFactory(configuration);
//         OpenAiSession openSession = factory.openSession();
//         System.out.println("我是chatGPT，请对我提问");
//         ChatGPTCompletionRequest chatCompletionRequest = ChatGPTCompletionRequest
//                 .builder()
//                 .messages(new ArrayList<>())
//                 .model(ChatGPTCompletionRequest.Model.GPT_3_5_TURBO.getCode())
//                 .user("test1")
//                 .build();
//         //通过控制台输入
//         Scanner scanner = new Scanner(System.in);
//         int i = 0;
//         while(scanner.hasNextLine()){
//             String text = scanner.nextLine();
//
//             //进行异步的获取结果
//             CompletableFuture<ChatGPTCompletionResponse> futureResponse = CompletableFuture.supplyAsync(() -> {
//                 chatCompletionRequest.getMessages().add(Message.builder().role(Constants.Role.USER).content(text).build());
//                 return openSession.chatCompletions(chatCompletionRequest);
//             });
//             //如果没有异步的调用没有完成，执行下面的逻辑
//             int index = 0;
//             while(!futureResponse.isDone()){
//                 char[] spinners = {'⠇', '⠋', '⠙', '⠸', '⠴', '⠦', '⠇', '⠋'};
//
//                 System.out.print("正在思考："+"\r" + spinners[index++ % spinners.length]);
//                 try {
//                     Thread.sleep(100);
//                 } catch (InterruptedException e) {
//                     throw new RuntimeException(e);
//                 }
//             }
//             System.out.println();
//
//             ChatGPTCompletionResponse chatCompletionResponse = futureResponse.get();
//             //讲每次最新的回答提交到提问当中，就可以进行上下文的回答
//             chatCompletionRequest.getMessages().add(Message.builder().role(Constants.Role.USER).content(chatCompletionResponse.getChoices().get(0).getMessage().getContent()).build());
//             System.out.println(chatCompletionResponse.getChoices().get(0).getMessage().getContent());
//             System.out.println("第"+(++i)+"次问题");
//             System.out.println( chatCompletionRequest.getMessages());
//         }
//
//
//     }
//
// }
