/*
 * Copyright (c) 2018, 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.oracle.truffle.js.runtime.objects;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.Message;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.nodes.RootNode;

final class NullOrUndefinedForeignAccessFactory implements ForeignAccess.Factory, ForeignAccess.StandardFactory {

    private static final ForeignAccess FOREIGN_ACCESS = ForeignAccess.create(null, new NullOrUndefinedForeignAccessFactory());

    private NullOrUndefinedForeignAccessFactory() {
    }

    static ForeignAccess getForeignAccess() {
        return FOREIGN_ACCESS;
    }

    @Override
    public boolean canHandle(TruffleObject o) {
        return o == Null.instance || o == Undefined.instance;
    }

    @Override
    public CallTarget accessMessage(Message tree) {
        return null;
    }

    @Override
    public CallTarget accessRead() {
        return createCallTarget(new UnsupportedMessageNode(Message.READ));
    }

    @Override
    public CallTarget accessWrite() {
        return createCallTarget(new UnsupportedMessageNode(Message.WRITE));
    }

    @Override
    public CallTarget accessExecute(int argumentsLength) {
        return createCallTarget(new UnsupportedMessageNode(Message.EXECUTE));
    }

    @Override
    public CallTarget accessInvoke(int argumentsLength) {
        return createCallTarget(new UnsupportedMessageNode(Message.INVOKE));
    }

    @Override
    public CallTarget accessIsExecutable() {
        return createCallTarget(new ValueNode(false));
    }

    @Override
    public CallTarget accessIsNull() {
        return createCallTarget(new ValueNode(true));
    }

    @Override
    public CallTarget accessHasSize() {
        return createCallTarget(new ValueNode(false));
    }

    @Override
    public CallTarget accessGetSize() {
        return null;
    }

    @Override
    public CallTarget accessIsBoxed() {
        return createCallTarget(new ValueNode(false));
    }

    @Override
    public CallTarget accessUnbox() {
        return null;
    }

    @Override
    public CallTarget accessNew(int argumentsLength) {
        return createCallTarget(new UnsupportedMessageNode(Message.NEW));
    }

    @Override
    public CallTarget accessKeys() {
        return createCallTarget(new UnsupportedMessageNode(Message.KEYS));
    }

    @Override
    public CallTarget accessKeyInfo() {
        return createCallTarget(new UnsupportedMessageNode(Message.KEY_INFO));
    }

    private static CallTarget createCallTarget(RootNode node) {
        return Truffle.getRuntime().createCallTarget(node);
    }

    private static class UnsupportedMessageNode extends RootNode {
        private final Message message;

        UnsupportedMessageNode(Message message) {
            super(null, null);
            this.message = message;
        }

        @Override
        public Object execute(VirtualFrame frame) {
            throw UnsupportedMessageException.raise(message);
        }
    }

    private static class ValueNode extends RootNode {
        private final Object value;

        ValueNode(Object value) {
            super(null, null);
            this.value = value;
        }

        @Override
        public Object execute(VirtualFrame frame) {
            return value;
        }
    }
}
