/***
 * Copyright 2013 Teoti Graphix, LLC.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * 
 * @author Michael Schmalle <mschmalle@teotigraphix.com>
 */

package randori.compiler.internal.codegen.js.emitter;

import org.apache.flex.compiler.definitions.IDefinition;
import org.apache.flex.compiler.internal.scopes.ASScope;
import org.apache.flex.compiler.internal.tree.as.IdentifierNode;
import org.apache.flex.compiler.projects.ICompilerProject;
import org.apache.flex.compiler.tree.as.IDynamicAccessNode;

import randori.compiler.codegen.js.IRandoriEmitter;
import randori.compiler.codegen.js.ISubEmitter;
import randori.compiler.internal.utils.DefinitionNameUtils;
import randori.compiler.internal.utils.ExpressionUtils;

/**
 * Handles the production of the {@link IDynamicAccessNode}.
 * 
 * @author Michael Schmalle
 */
public class DynamicAccessEmitter extends BaseSubEmitter implements
        ISubEmitter<IDynamicAccessNode>
{

    public DynamicAccessEmitter(IRandoriEmitter emitter)
    {
        super(emitter);
    }

    @Override
    public void emit(IDynamicAccessNode node)
    {
        getModel().setInAssignment(false);
        ICompilerProject project = getEmitter().getWalker().getProject();
        IDefinition definition = node.getLeftOperandNode().resolve(project);
        if (definition != null && node.getLeftOperandNode() instanceof IdentifierNode && definition.getTypeReference() != null
            && definition.getTypeReference().getName().indexOf("ByteArray") > -1)
        {
            getEmitter().getWalker().walk(node.getLeftOperandNode());
            write(".getValueByPosition(");
            getEmitter().getWalker().walk(node.getRightOperandNode());
            write(")");
            return;
        }
        getEmitter().getWalker().walk(node.getLeftOperandNode());
        write("[");
        getEmitter().getWalker().walk(node.getRightOperandNode());
        write("]");
    }

}
