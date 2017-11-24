package cz.pfservis.hosys;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import cz.pfservis.hosys.enums.HosysPage;
import hosys.pfservis.cz.hosys.R;

/**
 * Created by petr on 26.12.16.
 */
@RunWith(MockitoJUnitRunner.class)
public class HosysDownloaderTest {

    private static final String FAKE_STRING = "Aktualizace";

    @Mock
    Context mMockContext;

    @Test
    public void readStringFromContext_LocalizedString() {
        // Given a mocked Context injected into the object under test...
        when(mMockContext.getString(R.string.action_refresh)).thenReturn(FAKE_STRING);

//        HosysHtmlText hosysText = new HosysHtmlText() {
//            @Override
//            public void processHtmlText(HosysHtmlProcesor hosysHtmlProcesor) {
//                String result = hosysHtmlProcesor.getHtml();
//
//                System.out.println(result);
//
//                assertThat(result, is(FAKE_STRING));
//
//            }
//        };
//
//        HosysDownloader hosysDownloader = new HosysDownloader(hosysText, "", mMockContext);
//
//        hosysDownloader.execute(HosysPage.rozpis);
    }
}