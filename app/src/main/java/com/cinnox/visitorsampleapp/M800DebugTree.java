package com.cinnox.visitorsampleapp;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import timber.log.Timber;

class M800DebugTree extends Timber.DebugTree {
    @Override
    protected void log(int priority, @Nullable String tag, @NotNull String message, @Nullable Throwable t) {
        // console logging
        super.log(priority, tag, message, t);
    }

    @Override
    protected @Nullable String createStackElementTag(@NotNull StackTraceElement element) {
        return String.format("%s:%s",
                super.createStackElementTag(element),
                element.getLineNumber());
    }
}
