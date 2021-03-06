package com.inverce.mod.core;

import android.content.Context;
import android.support.annotation.NonNull;

import com.inverce.mod.core.internal.IMInternal;

/**
 * Utility class to initialize Inverce's Mod, for cases when automatic initialization does not apply.
 */
public class IMInitializer {
    /**
     * Initialize Inverce's Mod
     *
     * @param context the context
     */
    public static void initialize(@NonNull Context context) {
        IMInternal.get().initialize(context);
    }
}
