/*******************************************************************************
 *
 *  Copyright (c) 2014 , Hookflash Inc.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those
 *  of the authors and should not be interpreted as representing official policies,
 *  either expressed or implied, of the FreeBSD Project.
 *******************************************************************************/
package com.openpeer.sample.conversation;

import android.database.Cursor;

import com.openpeer.sdk.model.HOPDataManager;
import com.openpeer.sdk.datastore.DatabaseContracts.WindowViewEntry;

public class ChatInfo {

    private String mNameString;
    private String mLastMessage;
    private long mLastMessageTime;
    private int mUnreadCount;
    private long mUserIDs[];
    private long mRolodexIDs[];

    private String mType;
    private long mId;
    private String mConversationId;

    public String getType() {
        return mType;
    }

    public void setType(String mType) {
        this.mType = mType;
    }

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }


    public String getConversationId() {
        return mConversationId;
    }

    public void setConversationId(String id) {
        this.mConversationId = id;
    }

    private String mAvatarUri[];

    public String getNameString() {
        return mNameString;
    }

    public String getLastMessage() {
        return mLastMessage;
    }

    public long getLastMessageTime() {
        return mLastMessageTime;
    }

    public int getUnreadCount() {
        return mUnreadCount;
    }

    public long[] getUserIDs() {
        return mUserIDs;
    }

    public ChatInfo(long id,
                    String type,
                    String conversationId,
                    long[] userIds,
                    long[] rolodexIds,
                    String nameString,
                    String lastMessage,
                    long mLastMessageTime,
                    int unreadCount,
                    String avatarUri[]) {
        super();
        this.mType = type;
        this.mConversationId = conversationId;
        this.mUserIDs = userIds;
        this.mId = id;
        this.mRolodexIDs = rolodexIds;
        this.mNameString = nameString;
        this.mLastMessage = lastMessage;
        this.mLastMessageTime = mLastMessageTime;
        this.mUnreadCount = unreadCount;
        this.mAvatarUri = avatarUri;
    }

    public static ChatInfo fromCursor(Cursor cursor) {
        long id = cursor.getLong(0);
        String type = cursor.getString(cursor.getColumnIndex(WindowViewEntry
                                                                 .COLUMN_CONVERSATION_TYPE));
        String conversationId = cursor.getString(cursor.getColumnIndex(WindowViewEntry
                                                                           .COLUMN_CONVERSATION_ID));
        long cbcId = cursor.getLong(cursor.getColumnIndex(WindowViewEntry.COLUMN_CBC_ID));
        String nameString = cursor.getString(cursor
                                                 .getColumnIndex(WindowViewEntry
                                                                     .COLUMN_PARTICIPANT_NAMES));
        String lastMessage = cursor.getString(cursor
                                                  .getColumnIndex(WindowViewEntry
                                                                      .COLUMN_LAST_MESSAGE));
        long lastMessageTime = cursor.getLong(cursor
                                                  .getColumnIndex(WindowViewEntry
                                                                      .COLUMN_LAST_MESSAGE_TIME));


        long userIds[]= stringToLongArray( cursor.getString(cursor.getColumnIndex(WindowViewEntry
                                                                         .COLUMN_USER_ID)));
        long rolodexIds[]= stringToLongArray( cursor.getString(cursor.getColumnIndex(WindowViewEntry
                                                                         .COLUMN_ROLODEX_ID)));

        int mUnreadCount = cursor.getInt(cursor
                                             .getColumnIndex(WindowViewEntry.COLUMN_UNREAD_COUNT));
        ChatInfo ci = new ChatInfo(id,
                                   type,
                                   conversationId,
                                   userIds,
                                   rolodexIds,
                                   nameString,
                                   lastMessage,
                                   lastMessageTime,
                                   mUnreadCount, null);

        return ci;
    }

    public String getAvatarUri(int width, int height) {
        // todo: Support proper selection of avatar url
        long rolodexId = mRolodexIDs[0];
        String uri = HOPDataManager.getInstance().getAvatarUri(rolodexId,
                                                                       width, height);
        return uri;
    }

    static long[] stringToLongArray(String str){
        String idStrings[] = str.split(",");

        long IDs[] = new long[idStrings.length];
        for (int i = 0; i < IDs.length; i++) {
            IDs[i] = Long.parseLong(idStrings[i]);
        }
        return IDs;
    }
}
