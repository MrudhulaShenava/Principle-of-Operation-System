The JNachos program has to be converted from an uniprogramming model to a multiprogramming model.
To do that, the JNachos.startProcess() needs to be called multiple times for every files given as an argument.The startProcess() calls Machine.Run(), from which the control never returns to startProcess() again. The only way to make the JNachos code to work as a multiprogramming model, is to make a startProcess() run for every argument value.
The ProcessTest() constructor calls the fork() to create 5 threads of execution. Hence, in order to execute multiprogramming we need to invoke the ProcessTest() constructor from the Main.java file. In the ProcessTest() the current process wil be forked and once the execution of the thread is complete, the control returns and another processs will be executed.


Collaborated with Sheeraja Rajakrishnan