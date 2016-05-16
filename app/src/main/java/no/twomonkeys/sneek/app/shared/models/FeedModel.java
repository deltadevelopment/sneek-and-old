package no.twomonkeys.sneek.app.shared.models;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import no.twomonkeys.sneek.app.shared.SimpleCallback;
import no.twomonkeys.sneek.app.shared.helpers.AuthHelper;
import no.twomonkeys.sneek.app.shared.helpers.DataHelper;
import no.twomonkeys.sneek.app.shared.helpers.DateHelper;
import no.twomonkeys.sneek.app.shared.helpers.GenericContract;
import no.twomonkeys.sneek.app.shared.helpers.NetworkHelper;

/**
 * Created by simenlie on 12.05.16.
 */
public class FeedModel extends CRUDModel {

    private int index;
    private boolean isTop;
    private ArrayList<StoryModel> stories;
    private StoryModel deviceUserStory;
    private int width, height;

    public FeedModel(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);


        //Get the full size of an iOS device
        //750 width x 1334 height
        float dimensionWidth = 750;
        float dimensionHeight = 1334;

        float percentageOfWidth = (dimensionHeight/dimensionWidth)*100;
        float heightThen =  ((float) size.x * percentageOfWidth) / 100;
        Log.v("FEEDMODEL", "Percentage of width " + percentageOfWidth + " " + heightThen);
        width = size.x;
        height = (int)heightThen;

        stories = new ArrayList<>();
    }

    public void build(Map JSONDictionary) {
        /*
        if (JSONDictionary instanceof Array)
        {
            Log.v("FEEDMODEL", "ERRor");
        }
*/
        Log.v("FEEDMODEL", "dic " + JSONDictionary);
        ArrayList<Map> storiesRaw = (ArrayList) JSONDictionary.get("stories");
        ArrayList<StoryModel> storiesTemp = new ArrayList();
        index = 0;
        Log.v("FEEDMODEL", "dic " + storiesRaw.size());
        for (Map storyRaw : storiesRaw) {
            StoryModel storyModel = new StoryModel(storyRaw);
            if (!hasExpired(storyModel)) {
                if (!DataHelper.isBlocked(storyModel)) {
                    for (StoryModel story : stories) {
                        if (story.id == storyModel.id) {
                            if (story.getMoments().size() != 0) {
                                MomentModel moment = story.getMoments().get(0);
                                MomentModel moment2 = storyModel.getMoments().get(0);
                                if (moment.id == moment2.id) {
                                    //Has the same moment, no need to add or replace
                                    storyModel = story;
                                }
                            }
                        }
                    }
                    storiesTemp.add(storyModel);
                    setFrameForStory(width, height, storyModel);
                    index++;
                }
                if (storyModel.getUserModel().getId() == AuthHelper.getUserId()) {
                    deviceUserStory = storyModel;
                }
            }
        }
        Log.v("FEED", "COUNT " + storiesTemp.size());
        stories = storiesTemp;
        // [self.stories addObject:storyModel];
    }

    public boolean hasExpired(StoryModel story) {
        MomentModel lastMoment = story.getMoments().get(0);
        return (DateHelper.hasExpired(lastMoment.getCreated_at()));
    }

    public void fetch(final SimpleCallback scb) {
        String selectedFeed = DataHelper.currentFeed() == 0 ? "nearby" : "following";
        Log.v("FEED", selectedFeed);
        NetworkHelper.sendRequest(NetworkHelper.userService.getFeed(selectedFeed), GenericContract.get_feed(), onDataReturned(), scb);
    }


    public void setFrameForStory(int width, int height, StoryModel storyModel) {
        calc(width, height, storyModel);
        calcBig(width, height, storyModel);
    }


    public void calc(int w, int h, StoryModel story) {
        Random rand = new Random();
        //new algorithm
        int isOdd = (index % 2);

        //The max padding x position
        int maxPaddingX = isOdd == 1 ? 20 : 10;
        int xPos = rand.nextInt(maxPaddingX);

        //Y position should be max 20% the sise of the image
        float maxPercentY = 5;

        if (isOdd == 0) {
            isTop = !isTop;
            if (isTop) {
                maxPercentY = 10;
            }
        }

        Point storySize = sizeForStory(w, h);

        float twentyPercentage2 = (storySize.y * maxPercentY) / 100;

        //random percentage
        int percentageOfTopY = rand.nextInt(100);

        //The position where Y should start
        float startY = (twentyPercentage2 * percentageOfTopY) / 100;

        story.setFrame(new Rect(xPos, (int) startY, storySize.x, storySize.y));


        //x, y, width, height
        //left, top, right, bottom

        //the cellsize should be up to twenty percent bigger height
        story.setCellSize(new Point(isOdd == 1 ? storySize.x : storySize.x + xPos, (int) (storySize.y + twentyPercentage2)));
    }

    public Point sizeForStory(int w, int h) {
        float padding = 5;
        float width = w / 2;
        float height = h / 2;

        //Padding is 5, find how much 5 pixel from the width is in height
        float percentageOfWidth = (padding / width) * 100;
        float heightPixels = (height * percentageOfWidth) / 100;

        //the finished max height and width
        float resultWidth = width - padding;
        float resultHeight = height - heightPixels;

        return new Point((int) resultWidth, (int) resultHeight);
    }

    public Point sizeForBigStory(int w, int h) {
        float padding = 5;
        float width = w / 1.3f;
        float height = h / 1.3f;

        //Padding is 5, find how much 5 pixel from the width is in height
        float percentageOfWidth = (padding / width) * 100;
        float heightPixels = (height * percentageOfWidth) / 100;

        //the finished max height and width
        float resultWidth = width - padding;
        float resultheight = height - heightPixels;

        return new Point((int) resultWidth, (int) resultheight);
    }

    public void calcBig(int w, int h, StoryModel storyModel) {
        Random rand = new Random();

        int isOdd = (index % 2);

        Point storySize = sizeForBigStory(w, h);

        //The max padding x position
        int maxPaddingX = w - storySize.x;
        int xPos = rand.nextInt(maxPaddingX);

        float maxPercentY = 3;

        float twentyPercentage2 = (storySize.y * maxPercentY) / 100;

        //random percentage
        int percentageOfTopY = rand.nextInt(100);

        //The position where Y should start
        float startY = (twentyPercentage2 * percentageOfTopY) / 100;

        storyModel.setBigFrame(new Rect(xPos, (int) startY, storySize.x, storySize.y));
        //the cellsize should be up to twenty percent bigger height
        storyModel.setBigCellSize(new Point(w, (int) (storySize.y + twentyPercentage2)));
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public ArrayList<StoryModel> getStories() {
        return stories;
    }
}
