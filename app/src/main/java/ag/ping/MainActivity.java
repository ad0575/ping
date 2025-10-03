package ag.ping;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {
    private MainI18N i18n;
    private TextView output;
    private Handler handler;
    private Executor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);

        Button btn = findViewById(R.id.host_btn);

        i18n = new MainI18N(getResources());
        output = findViewById(R.id.output);
        handler = new Handler(Looper.getMainLooper());
        executor = Executors.newSingleThreadExecutor();

        btn.setOnClickListener((View view) -> {
            EditText host = findViewById(R.id.host);
            String target = host.getText().toString();

            if (target.isEmpty()) {
                output.append("\n"+ i18n.getHostNotInformed());
            }
            else {
                doPing(target);
            }
        });
        output.setMovementMethod(new ScrollingMovementMethod());
    }

    private void doPing(final String target) {
        handler.post(() ->
            output.append("\n"+ i18n.startPing(target))
        );
        executor.execute(() -> {
            Process process = null;
            try {
                ProcessBuilder builder = new ProcessBuilder("ping", "-c 4", target);
                process = builder.start();

                BufferedReader readerIn = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
                String line;

                // Show ping result.
                while ((line = readerIn.readLine()) != null) {
                    String finalLine = line;
                    handler.post(() ->
                        output.append("\n" + finalLine)
                    );
                }

                if (process.waitFor() != 0) { // Show ping error
                    BufferedReader errorReader = new BufferedReader(
                            new InputStreamReader(process.getErrorStream()));
                    int exitValue = process.exitValue();

                    handler.post(() ->
                        output.append("\n"+ i18n.pingFail(exitValue))
                    );

                    while ((line = errorReader.readLine()) != null) {
                        String finalLine = line;
                        handler.post(() ->
                            output.append("\n"+ finalLine)
                        );
                    }
                }
            }
            catch (Exception cause) {
                handler.post(() -> {
                    output.append("\n"+ i18n.callPingError());
                    output.append("\n"+ cause.getLocalizedMessage());
                });
            }
            finally {
                if (process != null) {
                    process.destroy();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
