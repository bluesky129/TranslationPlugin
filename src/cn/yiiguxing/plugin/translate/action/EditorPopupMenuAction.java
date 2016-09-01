package cn.yiiguxing.plugin.translate.action;

import cn.yiiguxing.plugin.translate.Utils;
import cn.yiiguxing.plugin.translate.ui.Icons;
import cn.yiiguxing.plugin.translate.ui.TranslationBalloon;
import com.intellij.codeInsight.editorActions.SelectWordUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.sun.istack.internal.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EditorPopupMenuAction extends AnAction implements DumbAware {

    @Nullable
    private TextRange mQueryTextRange;

    public EditorPopupMenuAction() {
        super("Translate", "Translate", Icons.Translate);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor editor = getEditor(e);
        if (editor != null && hasQueryTextRange()) {
            String queryText = Utils.splitWord(editor.getDocument().getText(mQueryTextRange));
            if (!Utils.isEmptyOrBlankString(queryText)) {
                SelectionModel selectionModel = editor.getSelectionModel();
                if (!selectionModel.hasSelection()) {
                    selectionModel.setSelection(mQueryTextRange.getStartOffset(), mQueryTextRange.getEndOffset());
                }

                new TranslationBalloon(editor).showAndQuery(queryText);
            }
        }
    }

    @Nullable
    private Editor getEditor(AnActionEvent e) {
        return CommonDataKeys.EDITOR.getData(e.getDataContext());
    }

    private boolean hasQueryTextRange() {
        return mQueryTextRange != null && !mQueryTextRange.isEmpty();
    }

    @Override
    public void update(AnActionEvent e) {
        mQueryTextRange = getQueryTextRange(e);
        e.getPresentation().setEnabledAndVisible(hasQueryTextRange());
    }

    @Nullable
    private TextRange getQueryTextRange(AnActionEvent e) {
        TextRange queryRange = null;

        Editor editor = getEditor(e);
        if (editor != null) {
            SelectionModel selectionModel = editor.getSelectionModel();
            if (!selectionModel.hasSelection()) {
                List<TextRange> ranges = new ArrayList<>();
                SelectWordUtil.addWordOrLexemeSelection(false, editor, editor.getCaretModel().getOffset(), ranges);

                if (!ranges.isEmpty()) {
                    TextRange maxRange = null;
                    for (TextRange range : ranges) {
                        if (maxRange == null || range.contains(maxRange)) {
                            maxRange = range;
                        }
                    }

                    queryRange = maxRange;
                }
            } else {
                queryRange = new TextRange(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd());
            }
        }

        return queryRange;
    }

}
