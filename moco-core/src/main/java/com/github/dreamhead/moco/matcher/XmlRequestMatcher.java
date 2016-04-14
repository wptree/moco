package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.*;
import com.github.dreamhead.moco.extractor.XmlExtractorHelper;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Optional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static com.google.common.base.Strings.isNullOrEmpty;

public class XmlRequestMatcher extends AbstractRequestMatcher {
    private final XmlExtractorHelper helper = new XmlExtractorHelper();
    private final DocumentBuilder documentBuilder;
    private final RequestExtractor<byte[]> extractor;
    private final Resource resource;

    public XmlRequestMatcher(final RequestExtractor<byte[]> extractor, final Resource resource) {
        this.extractor = extractor;
        this.resource = resource;
        this.documentBuilder = documentBuilder();
    }

    @Override
    public boolean match(final Request request) {
        try {
            Optional<Document> requestDocument = extractDocument(request, extractor);
            return requestDocument.isPresent() && tryToMatch(request, requestDocument.get());
        } catch (SAXException e) {
            return false;
        }
    }

    private boolean tryToMatch(Request request, Document document) throws SAXException {
        Document resourceDocument = getResourceDocument(request, this.resource);
        return document.isEqualNode(resourceDocument);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RequestMatcher doApply(final MocoConfig config) {
        if (config.isFor(resource.id())) {
            return new XmlRequestMatcher(this.extractor, resource.apply(config));
        }

        return this;
    }

    private Document getResourceDocument(final Request request, final Resource resource) throws SAXException {
        InputStream stream = resource.readFor(of(request)).toInputStream();
        return extractDocument(new InputSource(stream), documentBuilder);
    }

    private Optional<Document> extractDocument(final Request request,
                                     final RequestExtractor<byte[]> extractor) throws SAXException {
        Optional<InputSource> inputSourceOptional = helper.extractAsInputSource(request, extractor);
        if (!inputSourceOptional.isPresent()) {
            return absent();
        }

        return of(extractDocument(inputSourceOptional.get(), documentBuilder));
    }

    private void trimChild(final Node node, final Node child) {
        if (child instanceof Text) {
            if (isNullOrEmpty(child.getNodeValue().trim())) {
                node.removeChild(child);
            }
            return;
        }

        if (child instanceof Element) {
            trimNode(child);
        }
    }

    // Whitespace will be kept by DOM parser.
    private void trimNode(final Node node) {
        NodeList children = node.getChildNodes();
        for (int i = children.getLength() - 1; i >= 0; i--) {
            trimChild(node, children.item(i));
        }
    }

    private DocumentBuilder documentBuilder() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setCoalescing(true);
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setIgnoringComments(true);

        try {
            return dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new MocoException(e);
        }
    }

    private Document extractDocument(InputSource inputSource, DocumentBuilder documentBuilder) throws SAXException {
        try {
            Document document = documentBuilder.parse(inputSource);
            document.normalizeDocument();
            trimNode(document);
            return document;
        } catch (IOException e) {
            throw new MocoException(e);
        }
    }
}
